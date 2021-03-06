package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.ElectionOfficial;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodyLinkViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodySubtitleViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodyTextViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ElectionBodyTitleViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.HeaderViewHolder;
import com.votinginfoproject.VotingInformationProject.views.viewHolders.ReportErrorViewHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by max on 4/15/16.
 */
public class ElectionDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ELECTION_VIEW_HOLDER = 0x0;
    private static final int REPORT_ERROR_VIEW_HOLDER = 0x1;
    private static final int BODY_TITLE_VIEW_HOLDER = 0x2;
    private static final int BODY_PARENT_VIEW_HOLDER = 0x4;
    private static final int BODY_CHILD_TEXT_VIEW_HOLDER = 0x5;
    private static final int BODY_CHILD_LINK_VIEW_HOLDER = 0x6;

    private final Context mContext;
    private final ElectionDetailsPresenter mPresenter;
    private final boolean hasHeader;
    private final List<ListItem> parentListNodes;

    public ElectionDetailsRecyclerViewAdapter(Context context, ElectionDetailsPresenter presenter) {
        mContext = context;
        mPresenter = presenter;

        ElectionAdministrationBody localAdmin = mPresenter.getLocalAdmin();
        parentListNodes = getListNodesForBody(localAdmin, "Local");

        ElectionAdministrationBody stateAdmin = mPresenter.getStateAdmin();
        parentListNodes.addAll(getListNodesForBody(stateAdmin, "State"));

        Election election = mPresenter.getElection();
        hasHeader = (election != null);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case ELECTION_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_header, parent, false);
                viewHolder = new HeaderViewHolder(view);
                break;
            case REPORT_ERROR_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_report_error, parent, false);
                viewHolder = new ReportErrorViewHolder(view);
                break;
            case BODY_TITLE_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_body_title, parent, false);
                viewHolder = new ElectionBodyTitleViewHolder(view);
                break;
            case BODY_PARENT_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_body_subtitle, parent, false);
                viewHolder = new ElectionBodySubtitleViewHolder(view);
                break;
            case BODY_CHILD_LINK_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_body_link, parent, false);
                viewHolder = new ElectionBodyLinkViewHolder(view);
                break;
            case BODY_CHILD_TEXT_VIEW_HOLDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_election_body_text, parent, false);
                viewHolder = new ElectionBodyTextViewHolder(view);
                break;
            default:
                viewHolder = null;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder electionInformationViewHolder = (HeaderViewHolder) holder;
            Election election = mPresenter.getElection();

            if (election != null) {
                electionInformationViewHolder.setData(election.getName(), election.getFormattedDate());
            }
        } else if (holder instanceof ReportErrorViewHolder) {
            ReportErrorViewHolder errorViewHolder = (ReportErrorViewHolder) holder;
            errorViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.reportErrorClicked();
                }
            });
        } else {
            ListItem item = itemForListPosition(position);

            if (item.onItemClickListener != null) {
                holder.itemView.setOnClickListener(item.onItemClickListener);
            } else {
                holder.itemView.setOnClickListener(null);
                holder.itemView.setClickable(false);
            }

            if (holder instanceof ElectionBodySubtitleViewHolder) {
                ElectionBodySubtitleViewHolder viewHolder = (ElectionBodySubtitleViewHolder) holder;
                viewHolder.setTitle(item.mText);
                viewHolder.setImageResource(item.mImageId);
                viewHolder.setExpanded(item.isExpanded, false);
                viewHolder.isFirstSubtitle = item.isFirstSubtitle;

            } else if (holder instanceof ElectionBodyTitleViewHolder) {
                ElectionBodyTitleViewHolder viewHolder = (ElectionBodyTitleViewHolder) holder;
                viewHolder.setTitle(item.mText);
            } else if (holder instanceof ElectionBodyLinkViewHolder) {
                ElectionBodyLinkViewHolder viewHolder = (ElectionBodyLinkViewHolder) holder;
                viewHolder.setTitle(item.mText);
            } else if (holder instanceof ElectionBodyTextViewHolder) {
                ElectionBodyTextViewHolder viewHolder = (ElectionBodyTextViewHolder) holder;
                viewHolder.setTitle(item.mText);
            }
        }
    }

    private ListItem itemForListPosition(int position) {
        if (hasHeader) {
            position--;
        }

        if (position < 0 || position >= parentListNodes.size()) {
            return null;
        }

        return parentListNodes.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader) {
            return ELECTION_VIEW_HOLDER;
        } else {
            if (hasHeader) {
                position--;
            }

            if (position < parentListNodes.size()) {
                return parentListNodes.get(position).mViewType;
            }
        }

        return REPORT_ERROR_VIEW_HOLDER;
    }

    @Override
    public int getItemCount() {
        return 1 + (hasHeader ? 1 : 0) + parentListNodes.size();
    }

    public void collapseAll() {
        List<ListItem> parentNodes = new LinkedList<>();
        for (ListItem item : parentListNodes) {
            if (!item.isChild() && item.isExpanded) {
                parentNodes.add(item);
            }
        }

        for (ListItem item : parentNodes) {
            item.isExpanded = false;
            collapseListItem(item);
            notifyItemChanged(getRecyclerViewIndexForItem(item));
        }
    }

    public List<ListItem> getListNodesForBody(ElectionAdministrationBody body, String sectionName) {
        List<ListItem> toReturn = new ArrayList<>();
        ListItem firstSubtitleListItem = null;

        if (body != null) {
            ListItem sectionTitleNode = new ListItem(BODY_TITLE_VIEW_HOLDER, sectionName);
            toReturn.add(sectionTitleNode);

            List<ListItem> websitesChildItems = new LinkedList<>();
            if (body.electionRegistrationUrl != null) {
                websitesChildItems.add(new ListItem(BODY_CHILD_LINK_VIEW_HOLDER, mContext.getString(R.string.details_label_voter_registration_url), body.electionRegistrationUrl));
            }

            if (body.absenteeVotingInfoUrl != null) {
                websitesChildItems.add(new ListItem(BODY_CHILD_LINK_VIEW_HOLDER, mContext.getString(R.string.details_label_absentee_voting_url), body.absenteeVotingInfoUrl));
            }

            if (body.votingLocationFinderUrl != null) {
                websitesChildItems.add(new ListItem(BODY_CHILD_LINK_VIEW_HOLDER, mContext.getString(R.string.details_label_voting_location_finder_url), body.votingLocationFinderUrl));
            }

            if (body.electionRulesUrl != null) {
                websitesChildItems.add(new ListItem(BODY_CHILD_LINK_VIEW_HOLDER, mContext.getString(R.string.details_label_election_rules_url), body.electionRulesUrl));
            }

            if (websitesChildItems.size() > 0) {
                for (final ListItem child : websitesChildItems) {
                    child.onItemClickListener = new LinkClickListener(child);
                }

                ListItem websitesParent = new ListItem(BODY_PARENT_VIEW_HOLDER, mContext.getString(R.string.details_section_header_links));
                websitesParent.mImageId = R.drawable.ic_computer;
                websitesParent.onItemClickListener = new SubtitleClickListener(websitesParent);
                toReturn.add(websitesParent);

                websitesParent.mHiddenListItems.addAll(websitesChildItems);

                firstSubtitleListItem = websitesParent;
            }

            if (body.hoursOfOperation != null) {
                ListItem hoursParent = new ListItem(BODY_PARENT_VIEW_HOLDER, mContext.getString(R.string.details_label_hours_of_operation));
                hoursParent.mImageId = R.drawable.ic_hours;
                hoursParent.onItemClickListener = new SubtitleClickListener(hoursParent);
                toReturn.add(hoursParent);

                hoursParent.mHiddenListItems.add(new ListItem(BODY_CHILD_TEXT_VIEW_HOLDER, body.hoursOfOperation));

                if (firstSubtitleListItem == null) {
                    firstSubtitleListItem = hoursParent;
                }
            }

            if (body.getPhysicalAddress() != null) {
                ListItem addressParent = new ListItem(BODY_PARENT_VIEW_HOLDER, mContext.getString(R.string.details_label_physical_address));
                addressParent.mImageId = R.drawable.ic_address_marker;
                addressParent.onItemClickListener = new SubtitleClickListener(addressParent);
                toReturn.add(addressParent);

                ListItem addressChildItem = new ListItem(BODY_CHILD_TEXT_VIEW_HOLDER, body.getPhysicalAddress().toString());
                addressChildItem.onItemClickListener = new AddressClickListener(addressChildItem);
                addressParent.mHiddenListItems.add(addressChildItem);

                if (firstSubtitleListItem == null) {
                    firstSubtitleListItem = addressParent;
                }
            }

            if (body.electionOfficials != null && !body.electionOfficials.isEmpty()) {
                ListItem officialsParent = new ListItem(BODY_PARENT_VIEW_HOLDER, mContext.getString(R.string.details_label_election_officials));
                officialsParent.mImageId = R.drawable.ic_account_circle;
                officialsParent.onItemClickListener = new SubtitleClickListener(officialsParent);
                toReturn.add(officialsParent);

                for (ElectionOfficial official : body.electionOfficials) {
                    officialsParent.mHiddenListItems.add(new ListItem(BODY_CHILD_TEXT_VIEW_HOLDER, official.name));
                }

                if (firstSubtitleListItem == null) {
                    firstSubtitleListItem = officialsParent;
                }
            }
        }

        if (firstSubtitleListItem != null) {
            firstSubtitleListItem.isFirstSubtitle = true;
        }

        return toReturn;
    }

    private void subtitleClicked(ListItem item, View clickedView) {
        if (item.isExpanded) {
            item.isExpanded = false;
            collapseListItem(item);
        } else {
            item.isExpanded = true;
            expandListItem(item);
        }

        if (clickedView.getTag() != null) {
            ElectionBodySubtitleViewHolder subtitleViewHolder = (ElectionBodySubtitleViewHolder) clickedView.getTag();

            if (subtitleViewHolder != null) {
                subtitleViewHolder.setExpanded(item.isExpanded, true);
            }
        }
    }

    private void expandListItem(ListItem item) {
        int recyclerViewParentIndex = getRecyclerViewIndexForItem(item);

        int insertIndex = parentListNodes.indexOf(item) + 1;

        for (ListItem child : item.mHiddenListItems) {
            parentListNodes.add(insertIndex, child);
            insertIndex++;
        }

        notifyItemRangeInserted(recyclerViewParentIndex + 1, item.mHiddenListItems.size());
    }

    private int getRecyclerViewIndexForItem(ListItem item) {
        int position = parentListNodes.indexOf(item);

        if (position >= 0 && hasHeader) {
            position++;
        }

        return position;
    }

    private void collapseListItem(ListItem item) {
        int position = getRecyclerViewIndexForItem(item);

        for (ListItem child : item.mHiddenListItems) {
            parentListNodes.remove(child);
        }

        notifyItemRangeRemoved(position + 1, item.mHiddenListItems.size());

    }

    class ListItem {
        public final int mViewType;
        public final String mText;
        public View.OnClickListener onItemClickListener;
        public String mURLString;
        public int mImageId = 0;
        public boolean isExpanded = false;
        public boolean isFirstSubtitle = false;
        public final List<ListItem> mHiddenListItems = new ArrayList<>();

        public ListItem(int viewType, String text) {
            mViewType = viewType;
            mText = text;
        }

        public ListItem(int viewType, String text, String urlString) {
            mText = text;
            mViewType = viewType;
            mURLString = urlString;
        }

        public boolean isChild() {
            return (mViewType == BODY_CHILD_LINK_VIEW_HOLDER)
                    || (mViewType == BODY_CHILD_TEXT_VIEW_HOLDER);
        }
    }

    abstract class DetailClickListener implements View.OnClickListener {
        final ListItem mItem;

        public DetailClickListener(ListItem item) {
            mItem = item;
        }
    }

    class LinkClickListener extends DetailClickListener {
        public LinkClickListener(ListItem item) {
            super(item);
        }

        @Override
        public void onClick(View v) {
            mPresenter.linkSelected(mItem.mURLString);
        }
    }

    class AddressClickListener extends DetailClickListener {
        public AddressClickListener(ListItem item) {
            super(item);
        }

        @Override
        public void onClick(View v) {
            mPresenter.addressSelected(mItem.mText);
        }
    }

    class SubtitleClickListener extends DetailClickListener {
        public SubtitleClickListener(ListItem item) {
            super(item);
        }

        @Override
        public void onClick(View v) {
            subtitleClicked(mItem, v);
        }
    }
}
